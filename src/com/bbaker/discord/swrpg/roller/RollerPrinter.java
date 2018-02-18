package com.bbaker.discord.swrpg.roller;

import java.util.List;

import com.bbaker.discord.swrpg.die.Die;
import com.bbaker.discord.swrpg.die.DieResult;
import com.bbaker.discord.swrpg.die.TableResult;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.message.emoji.CustomEmoji;

public class RollerPrinter {
	DiscordApi api;
	
	public RollerPrinter(DiscordApi api) {
		this.api = api;
	}
	
	public String print(TableResult tr) {
		StringBuilder sb = new StringBuilder();
		List<DieResult> dice = tr.getDice();
		for(DieResult dr : dice) {
			sb.append(findEmoji(dr.getFace()));
		}
		
		if(dice.size() > 0) {
			sb.append("\n");
		}
		
		boolean netZero = true;
		
		int check = tr.getCheck();
		if(check > 0) {
			sb.append(buildSingleResult(Die.SUCCESS, check));
			netZero = false;
		} else if(check < 0){
			sb.append(buildSingleResult(Die.FAILURE, check));
			netZero = false;
		}
		
		int consequence = tr.getConsequence();
		if(consequence > 0) {
			sb.append(buildSingleResult(Die.ADVANTAGE, consequence));
			netZero = false;
		} else if(consequence < 0){
			sb.append(buildSingleResult(Die.THREAT, consequence));
			netZero = false;
		}
		
		if(tr.getTriumph() > 0) {
			sb.append(buildSingleResult(Die.TRIUMPH, tr.getTriumph()));
			netZero = false;
		}
		
		if(tr.getDespair() > 0) {
			sb.append(buildSingleResult(Die.DESPAIR, tr.getDespair()));
			netZero = false;
		}
		
		if(tr.getLightSide() > 0) {
			sb.append(buildSingleResult(Die.LIGHT, tr.getLightSide()));
			netZero = false;
		}
		
		if(tr.getDarkSide() > 0) {
			sb.append(buildSingleResult(Die.DARK, tr.getDarkSide()));
			netZero = false;
		}
		
		if(netZero) {
			sb.append("`All dice have cancelled out`");
		}
		
		return sb.toString();
	}

	private String findEmoji(String name) {
		List<CustomEmoji> emojies = api.getCustomEmojisByName(name);
		if(emojies.size() > 0) {
			return emojies.get(0).getMentionTag(); // I guess we only care about the first one found
		} else {
			return name; // I guess return the original name
		}
	}
	
	private String buildSingleResult(Die die, int count) {
		return findEmoji(die.getFace()) + Math.abs(count) + " ";
	}
}
