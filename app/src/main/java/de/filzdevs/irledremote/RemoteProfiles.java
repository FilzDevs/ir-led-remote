package de.filzdevs.irledremote;

import java.util.Arrays;
import java.util.List;

/**
 * Defines supported 24-key and 44-key RGB LED IR remote layouts and NEC command mappings.
 */
public final class RemoteProfiles {

    public static final int ADDR_EF00 = 0xEF00;
    public static final int ADDR_FF00 = 0xFF00;

    private static final int TEXT_WHITE = 0xFFFFFFFF;
    private static final int TEXT_DARK = 0xFF121216;
    private static final int BTN_DARK = 0xFF2B2D38;
    private static final int BTN_MODE = 0xFF353846;

    public static final class RemoteButton {
        public final String label;
        public final int bgColor;
        public final int textColor;
        public final int command;

        public RemoteButton(String label, int bgColor, int textColor, int command) {
            this.label = label;
            this.bgColor = bgColor;
            this.textColor = textColor;
            this.command = command;
        }
    }

    public static final class Profile {
        public final String id;
        public final String name;
        public final int address16;
        public final List<RemoteButton> buttons;

        public Profile(String id, String name, int address16, List<RemoteButton> buttons) {
            this.id = id;
            this.name = name;
            this.address16 = address16;
            this.buttons = buttons;
        }
    }

    private RemoteProfiles() {
    }

    public static List<Profile> getProfiles() {
        return Arrays.asList(
                new Profile("24_ef00", "24-Tasten (Standard 0xEF00)", ADDR_EF00, build24KeyButtons()),
                new Profile("24_ff00", "24-Tasten (Alternativ 0xFF00)", ADDR_FF00, build24KeyButtons()),
                new Profile("44_ff00", "44-Tasten (Standard 0xFF00)", ADDR_FF00, build44KeyButtons())
        );
    }

    private static List<RemoteButton> build24KeyButtons() {
        return Arrays.asList(
                // Row 1: Brightness & Power
                new RemoteButton("HELL +", BTN_DARK, TEXT_WHITE, 0x00),
                new RemoteButton("HELL -", BTN_DARK, TEXT_WHITE, 0x01),
                new RemoteButton("AUS", 0xFF373A47, TEXT_WHITE, 0x02),
                new RemoteButton("EIN", 0xFFD32F2F, TEXT_WHITE, 0x03),

                // Row 2: Primary + White
                new RemoteButton("R", 0xFFE53935, TEXT_WHITE, 0x04),
                new RemoteButton("G", 0xFF43A047, TEXT_WHITE, 0x05),
                new RemoteButton("B", 0xFF1E88E5, TEXT_WHITE, 0x06),
                new RemoteButton("W", 0xFFF5F5F5, TEXT_DARK, 0x07),

                // Row 3
                new RemoteButton("", 0xFFFF5722, TEXT_WHITE, 0x08),
                new RemoteButton("", 0xFF66BB6A, TEXT_DARK, 0x09),
                new RemoteButton("", 0xFF3949AB, TEXT_WHITE, 0x0A),
                new RemoteButton("FLASH", BTN_MODE, TEXT_WHITE, 0x0B),

                // Row 4
                new RemoteButton("", 0xFFFF7043, TEXT_DARK, 0x0C),
                new RemoteButton("", 0xFF26C6DA, TEXT_DARK, 0x0D),
                new RemoteButton("", 0xFF5E35B1, TEXT_WHITE, 0x0E),
                new RemoteButton("STROBE", BTN_MODE, TEXT_WHITE, 0x0F),

                // Row 5
                new RemoteButton("", 0xFFFFA726, TEXT_DARK, 0x10),
                new RemoteButton("", 0xFF00ACC1, TEXT_WHITE, 0x11),
                new RemoteButton("", 0xFF8E24AA, TEXT_WHITE, 0x12),
                new RemoteButton("FADE", BTN_MODE, TEXT_WHITE, 0x13),

                // Row 6
                new RemoteButton("", 0xFFFFEE58, TEXT_DARK, 0x14),
                new RemoteButton("", 0xFF29B6F6, TEXT_DARK, 0x15),
                new RemoteButton("", 0xFFEC407A, TEXT_WHITE, 0x16),
                new RemoteButton("SMOOTH", BTN_MODE, TEXT_WHITE, 0x17)
        );
    }

    private static List<RemoteButton> build44KeyButtons() {
        return Arrays.asList(
                // Row 1: Brightness, Play/Pause, Power
                new RemoteButton("HELL +", BTN_DARK, TEXT_WHITE, 0x5C),
                new RemoteButton("HELL -", BTN_DARK, TEXT_WHITE, 0x5D),
                new RemoteButton("PAUSE", BTN_DARK, TEXT_WHITE, 0x41),
                new RemoteButton("EIN/AUS", 0xFFD32F2F, TEXT_WHITE, 0x40),

                // Row 2: R G B W
                new RemoteButton("R", 0xFFE53935, TEXT_WHITE, 0x58),
                new RemoteButton("G", 0xFF43A047, TEXT_WHITE, 0x59),
                new RemoteButton("B", 0xFF1E88E5, TEXT_WHITE, 0x45),
                new RemoteButton("W", 0xFFF5F5F5, TEXT_DARK, 0x44),

                // Row 3
                new RemoteButton("", 0xFFFF5722, TEXT_WHITE, 0x54),
                new RemoteButton("", 0xFF66BB6A, TEXT_DARK, 0x55),
                new RemoteButton("", 0xFF3949AB, TEXT_WHITE, 0x49),
                new RemoteButton("", 0xFFF48FB1, TEXT_DARK, 0x48),

                // Row 4
                new RemoteButton("", 0xFFFF7043, TEXT_DARK, 0x50),
                new RemoteButton("", 0xFF26C6DA, TEXT_DARK, 0x51),
                new RemoteButton("", 0xFF5E35B1, TEXT_WHITE, 0x4D),
                new RemoteButton("", 0xFFF8BBD0, TEXT_DARK, 0x4C),

                // Row 5
                new RemoteButton("", 0xFFFFA726, TEXT_DARK, 0x1C),
                new RemoteButton("", 0xFF00ACC1, TEXT_WHITE, 0x1D),
                new RemoteButton("", 0xFF7B1FA2, TEXT_WHITE, 0x1E),
                new RemoteButton("", 0xFF81D4FA, TEXT_DARK, 0x1F),

                // Row 6
                new RemoteButton("", 0xFFFFEE58, TEXT_DARK, 0x18),
                new RemoteButton("", 0xFF0097A7, TEXT_WHITE, 0x19),
                new RemoteButton("", 0xFFEC407A, TEXT_WHITE, 0x1A),
                new RemoteButton("", 0xFFCE93D8, TEXT_DARK, 0x1B),

                // Row 7: RGB Up + Quick
                new RemoteButton("R +", BTN_DARK, 0xFFFF8A80, 0x14),
                new RemoteButton("G +", BTN_DARK, 0xFFB9F6CA, 0x15),
                new RemoteButton("B +", BTN_DARK, 0xFF82B1FF, 0x16),
                new RemoteButton("QUICK", BTN_MODE, TEXT_WHITE, 0x17),

                // Row 8: RGB Down + Slow
                new RemoteButton("R -", BTN_DARK, 0xFFFF8A80, 0x10),
                new RemoteButton("G -", BTN_DARK, 0xFFB9F6CA, 0x11),
                new RemoteButton("B -", BTN_DARK, 0xFF82B1FF, 0x12),
                new RemoteButton("SLOW", BTN_MODE, TEXT_WHITE, 0x13),

                // Row 9: DIY 1-3 + Auto
                new RemoteButton("DIY1", BTN_DARK, TEXT_WHITE, 0x0C),
                new RemoteButton("DIY2", BTN_DARK, TEXT_WHITE, 0x0D),
                new RemoteButton("DIY3", BTN_DARK, TEXT_WHITE, 0x0E),
                new RemoteButton("AUTO", BTN_MODE, TEXT_WHITE, 0x0F),

                // Row 10: DIY 4-6 + Flash
                new RemoteButton("DIY4", BTN_DARK, TEXT_WHITE, 0x08),
                new RemoteButton("DIY5", BTN_DARK, TEXT_WHITE, 0x09),
                new RemoteButton("DIY6", BTN_DARK, TEXT_WHITE, 0x0A),
                new RemoteButton("FLASH", BTN_MODE, TEXT_WHITE, 0x0B),

                // Row 11: Jump / Fade modes
                new RemoteButton("JUMP3", BTN_MODE, TEXT_WHITE, 0x04),
                new RemoteButton("JUMP7", BTN_MODE, TEXT_WHITE, 0x05),
                new RemoteButton("FADE3", BTN_MODE, TEXT_WHITE, 0x06),
                new RemoteButton("FADE7", BTN_MODE, TEXT_WHITE, 0x07)
        );
    }
}
