package be.hehehe.geekbot.commands;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import be.hehehe.geekbot.annotations.BotCommand;
import be.hehehe.geekbot.annotations.Help;
import be.hehehe.geekbot.annotations.Trigger;
import be.hehehe.geekbot.annotations.TriggerType;
import be.hehehe.geekbot.annotations.Triggers;
import be.hehehe.geekbot.utils.IRCUtils;

import com.google.common.collect.Lists;

@BotCommand
public class HelpCommand {

	@Inject
	@Triggers
	List<Method> triggers;

	@Trigger(value = "!help")
	public List<String> helpGeneral() {
		List<String> result = Lists.newArrayList();
		result.add(IRCUtils.bold("Triggers: "));
		Set<String> set = new LinkedHashSet<String>();
		for (Method m : triggers) {
			Trigger trigger = m.getAnnotation(Trigger.class);
			TriggerType type = trigger.type();
			if (type == TriggerType.EXACTMATCH
					|| type == TriggerType.STARTSWITH) {
				set.add(trigger.value().trim());
			}
		}
		result.add(StringUtils.join(set, " "));
		return result;
	}

	@Trigger(value = "!help", type = TriggerType.STARTSWITH)
	public List<String> helpCommand() {
		List<String> result = Lists.newArrayList();
		for (Method m : triggers) {
			if (m.isAnnotationPresent(Help.class)) {
				Trigger trigger = m.getAnnotation(Trigger.class);
				String help = m.getAnnotation(Help.class).value();
				if (StringUtils.isNotBlank(help)) {
					TriggerType type = trigger.type();
					String line = trigger.value();
					if (type == TriggerType.STARTSWITH) {
						line += " <arguments>";
					}
					line += " : ";
					line = IRCUtils.bold(line);
					line += help;
				}
			}
		}
		return result;
	}
}
