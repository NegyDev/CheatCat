package net.cheat.cat;

import net.cheat.cat.ui.impl.UIMainMenu;

import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException {
        //please run the program in root mode. otherwise there will be problems with memory search.
        new UIMainMenu().start();
    }
}
