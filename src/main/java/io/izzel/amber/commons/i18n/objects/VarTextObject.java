package io.izzel.amber.commons.i18n.objects;

import com.google.common.reflect.TypeToken;
import io.izzel.amber.commons.i18n.args.Arg;
import lombok.ToString;
import lombok.val;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@ToString
public class VarTextObject extends LocaleObject implements MetaObject {

    private static final Pattern VAR_TEXT = Pattern.compile("\\{(\\d+)}(((?!\\{\\d+}).)*)");

    private Text head;
    private Map<Integer, Text> tails = new LinkedHashMap<>();

    private VarTextObject(String text) {
        val matcher = VAR_TEXT.matcher(text);
        while (matcher.find()) {
            if (head == null) {
                val idx = matcher.start();
                head = TextSerializers.FORMATTING_CODE.deserialize(text.substring(0, idx));
            }
            val num = Integer.parseInt(matcher.group(1));
            val append = matcher.group(2);
            tails.put(num, TextSerializers.FORMATTING_CODE.deserialize(append));
        }
    }

    public static VarTextObject of(String text) {
        return new VarTextObject(text);
    }

    private Text build(Object... args) {
        val builder = Text.builder().append(head);
        for (val entry : tails.entrySet()) {
            val num = entry.getKey();
            val append = entry.getValue();
            builder.append(applyMeta(num, Arg.of(at(args, num)).toText(), args));
            builder.append(append);
        }
        return applyMeta(-1, builder.build(), args);
    }

    private Object at(Object[] arr, int idx) {
        return arr.length > idx ? arr[idx] : "{" + idx + "}";
    }

    @Override
    public void send(MessageReceiver receiver, Object... args) {
        receiver.sendMessage(build(args));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T mapAs(TypeToken<T> typeToken, Object... args) {
        if (typeToken.equals(TypeToken.of(Text.class)))
            return (T) build(args);
        else return null;
    }

    @Override
    public Text apply(Text text, Object... args) {
        return build(args);
    }

}
