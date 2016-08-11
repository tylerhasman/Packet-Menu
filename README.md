# Spigot-Packet-Menus

Many minecraft inventory menu's can be exploited and items can be removed and used. Sometimes a spigot update can change how an inventory event is handled and as an effect break a feature or bug fix that your inventory menu had. Packet Menu makes it impossible for players to steal items because at no point does the minecraft part of the server ever know about a packet menu being open or what a player is doing to it. The Packet Menu API is very easy to use, however, non-devs be warned this plugin does nothing on its own.

# Features 
- Impossible for players to steal items from the packet menu
- Supports anvil and regular chest menu's

# Examples

Create a chest menu:


        ChestPacketMenu menu = new ChestPacketMenu(9, "My Packet Menu", player);
      
        menu.addItem(0, new ItemStack(Material.BLAZE_ROD), new PacketMenuSlotHandler() {
          
            @Override
            public void onClicked(Player player, PacketMenu menu, Interaction interactionInfo) {
                player.sendMessage("You clicked the blaze rod!");
                menu.close(player);
            }
            menu.open(player);

        });
        

Create an anvil menu:

        AnvilPacketMenu menu = new AnvilPacketMenu(player);
      
        menu.setResult(new ItemStack(Material.BLAZE_ROD, 1));
        menu.setDefaultText("Enter a word: ");
      
        menu.setHandler(new AnvilPacketMenuHandler() {
          
            @Override
            public void onResult(String text, Player pl) {
                pl.sendMessage("The word you entered was "+text);
            }
        });
      
        menu.open(player);

