package io.github.redwallhp.athenagm.utilities;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder class to more easily create written books
 * getBook() returns a usable ItemStack
 */
public class BookBuilder {


    private ItemStack item;
    private BookMeta meta;
    private String defaultContents = "";


    /**
     * @param title The book title
     */
    public BookBuilder(String title) {
        this.item = new ItemStack(Material.WRITTEN_BOOK, 1);
        this.meta = (BookMeta) this.item.getItemMeta();
        this.meta.setTitle(title);
    }


    /**
     * Get the finished ItemStack built by BookBuilder
     * @return The usable ItemStack object
     */
    public ItemStack getBook() {
        this.item.setItemMeta(meta);
        return this.item;
    }


    /**
     * Set the book's author
     * @param author The book's author
     */
    public BookBuilder setAuthor(String author) {
        this.meta.setAuthor(author);
        return this;
    }


    /**
     * Set the default page contents to use if the string passed to
     * parseBookContents() is empty. Defaults to ""
     * @param defaultContents Page contents
     */
    public BookBuilder setDefaultContents(String defaultContents) {
        this.defaultContents = defaultContents;
        return this;
    }


    /**
     * Take a string and split it into book pages.
     * Formatting codes are parsed with "&" as the token.
     * Page breaks can be manually inserted with ">>>>>" (5 or more ">" symbols)
     * @param contents String to set as book pages
     */
    public BookBuilder setPagesFromString(String contents) {
        parseBookContents(contents);
        return this;
    }


    /**
     * Load a file's contents and split it into book pages.
     * Formatting codes are parsed with "&" as the token.
     * Page breaks can be manually inserted with ">>>>>" (5 or more ">" symbols)
     * @param file File to load
     */
    public BookBuilder setPagesFromFile(File file) {
        if (!file.exists()) {
            parseBookContents(defaultContents);
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = reader.readLine();
            }
            reader.close();
            parseBookContents(sb.toString());
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }


    /**
     * Splits a string into page-sized strings and sets them on the BookMeta.
     * Also handles manual page breaks and applies formatting codes.
     * For an unknown reason, &r does not reset formatting. &0 works as a substitute.
     * @param contents Book contents
     */
    private void parseBookContents(String contents) {
        if (contents == null || contents.length() < 1) {
            contents = defaultContents;
        }
        contents = ChatColor.translateAlternateColorCodes('&', contents);
        List<String> pages = new ArrayList<String>();
        String[] words = contents.split("(?=[\\s+])");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            // suppress extraneous newlines at the top of pages
            if (sb.length() < 1 && word.charAt(0) == '\n') {
                word = word.replace("\n", "");
            }
            // manual page splits on ">>>>>"
            if (word.matches("\\s>{5,}")) {
                pages.add(sb.toString());
                sb.delete(0, sb.length());
                continue;
            }
            // add word to page, unless it would go over the size limit, otherwise insert a page break first
            if ((sb.length() + word.length() + 1) < 255) {
                sb.append(word);
            } else {
                pages.add(sb.toString());
                sb.delete(0, sb.length());
                sb.append(word);
            }
        }
        // write remaining contents out to a new page before setting the meta
        if (sb.length() > 0) {
            pages.add(sb.toString());
        }
        this.meta.setPages(pages);
    }


}
