package io.github.redwallhp.athenagm.modules.spectator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class HelpBook extends ItemStack {


    private BookMeta book;


    public HelpBook(String title, String contents) {
        this.setType(Material.WRITTEN_BOOK);
        this.setAmount(1);
        this.book = (BookMeta) this.getItemMeta();
        book.setTitle(title);
        parseBookContents(contents);
        this.setItemMeta(this.book);
    }


    private void parseBookContents(String contents) {
        if (contents == null || contents.length() < 1) {
            contents = "This book will be populated with the contents of a helpbook.txt file in the plugin directory.";
        }
        contents = ChatColor.translateAlternateColorCodes('&', contents);
        List<String> pages = new ArrayList<String>();
        String[] words = contents.split(" ");
        StringBuffer sb = new StringBuffer();
        for (String word : words) {
            if ((sb.length() + word.length() + 1) < 255) {
                sb.append(word);
                sb.append(" ");
            } else {
                pages.add(sb.toString());
                sb.delete(0, sb.length());
                sb.append(word);
                sb.append(" ");
            }
        }
        book.setPages(pages);
    }


}
