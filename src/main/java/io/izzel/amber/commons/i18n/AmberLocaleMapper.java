package io.izzel.amber.commons.i18n;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.izzel.amber.commons.i18n.objects.*;
import io.izzel.amber.commons.i18n.objects.typed.ClickObject;
import io.izzel.amber.commons.i18n.objects.typed.HoverObject;
import io.izzel.amber.commons.i18n.objects.typed.RefObject;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
class AmberLocaleMapper { // todo 这个硬编码真丑，得重写

    private static final Pattern VAR_TEXT = Pattern.compile("(.*\\{\\d+}.*)+");
    private static final Set<String> META_OBJECT = ImmutableSet.of("text", "meta");

    private final Map<String, Consumer<LocaleObject>> references = new HashMap<>();

    private LocaleObject parseObject(ConfigurationNode node) {
        switch (node.getValueType()) {
            case SCALAR:
                val s = node.getValue().toString();
                if (VAR_TEXT.matcher(s).matches()) {
                    return VarTextObject.of(s);
                } else {
                    return SimpleStringObject.of(s);
                }
            case MAP:
                if (node.getChildrenMap().keySet().contains("type")) {
                    return parseTypedObject(node);
                }
                if (node.getChildrenMap().keySet().equals(META_OBJECT)) {
                    return parseMeta(node);
                }
                break;
            case LIST:
                val builder = ImmutableList.<LocaleObject>builder();
                for (val child : node.getChildrenList()) {
                    builder.add(parseObject(child));
                }
                return ListObject.of(builder.build());
            case NULL:
                return SimpleStringObject.of("null");
        }
        throw new RuntimeException(String.valueOf(node.getValue()));
    }

    private LocaleObject parseMeta(ConfigurationNode node) {
        val text = parseObject(node.getNode("text"));
        val meta = node.getNode("meta");
        val map = new LinkedHashMap<Integer, MetaObject>();
        for (val entry : meta.getChildrenMap().entrySet()) {
            val num = Integer.parseInt(String.valueOf(entry.getKey()));
            val obj = parseObject(entry.getValue());
            if (obj instanceof MetaObject) {
                map.put(num, ((MetaObject) obj));
            } else {
                log.warn("Bad meta {} while parsing node {}", obj, Arrays.toString(meta.getPath()));
            }
        }
        text.setMeta(map);
        return text;
    }

    private LocaleObject parseTypedObject(ConfigurationNode node) {
        val type = String.valueOf(node.getNode("type").getString());
        switch (type) {
            case "ref":
                val name = node.getNode("ref").getString();
                val ref = RefObject.of(name);
                references.put(name, ref.getCallback());
                return ref;
            case "hover":
                val hover = parseObject(node.getNode("hover"));
                return HoverObject.of(hover);
            case "click":
                var command = node.getNode("command").getString();
                if (command != null) {
                    return ClickObject.of(command, false);
                } else {
                    command = node.getNode("suggest").getString();
                    if (command != null) {
                        return ClickObject.of(command, true);
                    } else {
                        log.warn("Provide 'command' or 'suggest' in a click node, parsing node {}", Arrays.toString(node.getPath()));
                    }
                }
            default:
                log.warn("Unknown type {} while parsing node {}", type, Arrays.toString(node.getPath()));
                return SimpleStringObject.of("null");
        }
    }

    private void saveMap(ConfigurationNode node, Map<String, LocaleObject> map) {
        val key = Arrays.stream(node.getPath()).map(String::valueOf).collect(Collectors.joining("."));
        switch (node.getValueType()) {
            case LIST:
            case SCALAR:
            case NULL:
                map.put(key, parseObject(node));
                break;
            case MAP:
                if (node.getChildrenMap().keySet().contains("type") || node.getChildrenMap().keySet().equals(META_OBJECT)) {
                    map.put(key, parseObject(node));
                } else {
                    for (val child : node.getChildrenMap().values()) {
                        saveMap(child, map);
                    }
                }
                break;
        }
    }

    Map<String, LocaleObject> asMap(CommentedConfigurationNode node) {
        val map = new HashMap<String, LocaleObject>();
        saveMap(node, map);
        references.forEach((k, v) -> v.accept(map.get(k)));
        return map;
    }

}
