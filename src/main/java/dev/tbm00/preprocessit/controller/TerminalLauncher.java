package dev.tbm00.preprocessit.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerminalLauncher {
    private static boolean alreadyOpened = false;

    public static synchronized void openLogTailer(Path logFile) throws IOException {
        if (alreadyOpened) return;
        List<String> cmd = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            cmd.add("cmd.exe");
            cmd.add("/c");
            cmd.add("start");
            cmd.add("PreProcessIt Helper Log");  // empty title for the new window
            cmd.add("powershell.exe");
            cmd.add("-NoExit");
            cmd.add("-Command");
            cmd.add(String.format("Get-Content -Path '%s' -Wait", logFile.toString()));
        }
        else if (os.contains("mac")) {
            String appleCmd = String.format(
                "tell application \"Terminal\" to do script \"clear; tail -f '%s'\"", 
                logFile.toString()
            );
            cmd.addAll(Arrays.asList("osascript", "-e", appleCmd));
        }
        else {
            // Linux: try gnome‑terminal, else xterm
            boolean hasGnome = false;
            try {
                hasGnome = (new ProcessBuilder("which", "gnome-terminal")
                             .start()
                             .waitFor() == 0);
            } catch (InterruptedException ignored) {}

            if (hasGnome) {
                cmd.addAll(Arrays.asList(
                  "gnome-terminal", "--", "bash", "-c",
                  String.format("clear; tail -f '%s'; exec bash", logFile.toString())
                ));
            } else {
                cmd.addAll(Arrays.asList(
                  "xterm", "-hold", "-e",
                  String.format("tail -f '%s'", logFile.toString())
                ));
            }
        }

        new ProcessBuilder(cmd).start();
        alreadyOpened = true;
    }
}