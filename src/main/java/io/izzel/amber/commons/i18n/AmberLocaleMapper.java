package io.izzel.amber.commons.i18n;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.izzel.amber.commons.i18n.objects.ListObject;
import io.izzel.amber.commons.i18n.objects.LocaleObject;
import io.izzel.amber.commons.i18n.objects.SimpleStringObject;
import io.izzel.amber.commons.i18n.objects.VarTextObject;
import lombok.experimental.UtilityClass;
import lombok.val;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
class AmberLocaleMapper {

    private final Pattern VAR_TEXT = Pattern.compile("(.*\\{\\d+}.*)+");
    private final Set<String> META_OBJECT = ImmutableSet.of("text", "meta");

    LocaleObject parseObject(ConfigurationNode node) {
        switch (node.getValueType()) {
            case SCALAR:
                val s = node.getValue().toString();
                if (VAR_TEXT.matcher(s).matches()) {
                    return VarTextObject.of(s);
                } else {
                    return SimpleStringObject.of(s);
                }
            case MAP: break;
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

    LocaleObject parseTypedObject(ConfigurationNode node) {
        return null; // todo
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
                if (node.getChildrenMap().keySet().contains("type")) {
                    map.put(key, parseTypedObject(node));
                } else if (node.getChildrenMap().keySet().equals(META_OBJECT)) {
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
        return map;
    }

}
