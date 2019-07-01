package io.izzel.amber.commons.i18n.objects;

import com.google.common.reflect.TypeToken;
import io.izzel.amber.commons.i18n.args.Arg;
import lombok.val;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class VarTextObject implements LocaleObject {

    private static final Pattern VAR_TEXT = Pattern.compile("\\{(\\d+)}(((?!\\{\\d+}).)*)");

    private Text head;
    private Map<Integer, Text> tails = new LinkedHashMap<>();
    private String text;

    private VarTextObject(String text) {
        this.text = text;
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

    @Override
    public void send(MessageReceiver receiver, Object... args) {
        val builder = Text.builder().append(head);
        for (val entry : tails.entrySet()) {
            val num = entry.getKey();
            val append = entry.getValue();
            builder.append(Arg.of(at(args, num)).toText());
            builder.append(append);
        }
        val text = builder.build();
        receiver.sendMessage(text);
    }

    private Object at(Object[] arr, int idx) {
        return arr.length > idx ? arr[idx] : "{"+idx+"}";
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T raw() {
        return ((T) text);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T mapAs(TypeToken<T> typeToken) {
        if (typeToken.equals(TypeToken.of(String.class)))
            return raw();
        else if (typeToken.equals(TypeToken.of(Text.class))) {
            return (T) Text.of(text);
        } else return null;
    }
}
