package com.example.demo.system;

import com.example.demo.model.utils.DialogueLoader;
import com.example.demo.view.ui.DialogueBox;

public class DialogueSystem {
    private DialogueLoader loader;
    private DialogueBox dialogueBox;

    public DialogueSystem(String filePath) {
        loader = new DialogueLoader(filePath);
    }

    public void start() {
        String cmd;

        while ((cmd = loader.getNextValidLine()) != null){
            if (cmd.startsWith("+background:")) {
                String path = cmd.substring(12).trim();
                System.out.println("IntroSystem: Setting background to " + path);
                //doi background qua text
            } else if (cmd.startsWith("-text")) {
                try {
                    String content = cmd.substring(6).trim();
                    int targetIndex = content.indexOf(":");

                    String character = content.substring(0,targetIndex).trim().toUpperCase();
                    String sentence = content.substring(targetIndex + 1).trim();

                    DialogueBox.DialogueLine.Speaker speaker = DialogueBox.DialogueLine.getByName(character);
                    DialogueBox.DialogueLine dialogue = new DialogueBox.DialogueLine(speaker, sentence);
                } catch (StringIndexOutOfBoundsException e) {
                    System.err.println("FATAL ERROR: Malformed '-text' command in script. " +
                            "Missing ':'. Line: \"" + cmd + "\"");
                    // stop intro here
                    return;
                } catch (IllegalAccessError e) {
                    System.err.println("FATAL ERROR: Invalid speaker name in script." +
                            " Line: \"" + cmd + "\"");
                    return;
                }
            }
        }
    }


}
