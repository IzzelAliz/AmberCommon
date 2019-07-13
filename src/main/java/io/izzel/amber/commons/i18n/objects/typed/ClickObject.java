package io.izzel.amber.commons.i18n.objects.typed;

import com.google.common.reflect.TypeToken;
import io.izzel.amber.commons.i18n.objects.LocaleObject;
import io.izzel.amber.commons.i18n.objects.MetaObject;
import io.izzel.amber.commons.i18n.objects.SimpleStringObject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;

@ToString
@RequiredArgsConstructor(staticName = "of")
public class ClickObject extends LocaleObject implements MetaObject {

    private final String command;

    @Override
    public void send(MessageReceiver receiver, Object... args) {
        receiver.sendMessage(Text.of("Click: ", command));
    }

    @Override
    public <T> T mapAs(TypeToken<T> typeToken, Object... args) {
        return SimpleStringObject.of(replace(command, args)).mapAs(typeToken, args);
    }

    @Override
    public Text apply(Text text, Object... args) {
        return Text.builder().append(text).onClick(TextActions.runCommand(replace(command, args))).build();
    }

    // this is a lot faster than the internal formatter
    private String replace(String template, Object... args) {
        if (args.length == 0 || template.length() == 0) {
            return template;
        }
        val arr = template.toCharArray();
        val stringBuilder = new StringBuilder(template.length());
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '{' && Character.isDigit(arr[Math.min(i + 1, arr.length - 1)])
                    && arr[Math.min(i + 1, arr.length - 1)] - '0' < args.length
                    && arr[Math.min(i + 2, arr.length - 1)] == '}'
                    && args[arr[i + 1] - '0'] != null) {
                stringBuilder.append(args[arr[i + 1] - '0']);
                i += 2;
            } else {
                stringBuilder.append(arr[i]);
            }
        }
        return stringBuilder.toString();
    }

}
