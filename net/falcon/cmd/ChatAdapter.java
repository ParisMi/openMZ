package net.falcon.cmd;

import java.util.Arrays;

import net.falcon.MZStrings;
import net.falcon.OpenMZ;
import net.falcon.util.MZUtil;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ChatAdapter extends PacketAdapter 	{


	public ChatAdapter() {
		super(OpenMZ.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT);
	}


	//omz config......
	public static void handleConfigCommand(Player origin, String[] args) {
		if(args.length == 1 && args[0].equals("config")) { //just /omz config
			MZUtil.setMetadata(origin, MZStrings.METADATA_CONFIGMODE, true);
			DisplayMenu.displayMenu(new MainMenu(), origin);
			return;
		}


		if(args.length == 2 && args[1].equals("quit")) {
			MZUtil.quitConfigMode(origin);
			return;
		}
		if(args[0].equals("config")) {
			MZUtil.getMenu(origin).onCommand(Arrays.asList(args).subList(1, args.length), origin);
		} else {
			MZUtil.getMenu(origin).onCommand(Arrays.asList(args), origin);
		}

	}

	@Override
	public void onPacketSending(PacketEvent e) {
		PacketContainer pack = e.getPacket();
		String chat = pack.getChatComponents().read(0).getJson();
		Boolean bypass = false;
		if(chat.contains(MZStrings.HIDECHAT_BYPASS)) {
			bypass = true;
		}
		pack.getChatComponents().write(0, WrappedChatComponent.fromJson(chat.replace(MZStrings.HIDECHAT_BYPASS, "")));

		if((Boolean)MZUtil.getMetadata(e.getPlayer(), MZStrings.METADATA_CONFIGMODE, false) && !bypass) {
			MZUtil.addLineToMissedChat(e.getPlayer(), chat);
			e.setCancelled(true);
		}
	}
}
