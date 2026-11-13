package de.filzdevs.irledremote;

import android.content.Context;
import android.hardware.ConsumerIrManager;

/**
 * Encodes and transmits NEC infrared frames at 38 kHz via Android's ConsumerIrManager.
 */
public final class NecIrEncoder {

    private static final int CARRIER_HZ = 38000;
    private static final int LEADER_MARK_US = 9000;
    private static final int LEADER_SPACE_US = 4500;
    private static final int BIT_MARK_US = 562;
    private static final int ZERO_SPACE_US = 562;
    private static final int ONE_SPACE_US = 1687;

    private final ConsumerIrManager irManager;

    public NecIrEncoder(Context context) {
        this.irManager = (ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
    }

    public boolean hasIrEmitter() {
        return irManager != null && irManager.hasIrEmitter();
    }

    /**
     * Transmits a standard 32-bit NEC frame (LSB-first per byte):
     * [addrLow, addrHigh, command, ~command].
     *
     * @param address16 16-bit little-endian NEC address (e.g. 0xEF00 or 0xFF00)
     * @param command   8-bit NEC command byte (0x00 - 0xFF)
     * @return true if transmitted, false if no IR emitter is available
     */
    public boolean sendNec(int address16, int command) {
        if (!hasIrEmitter()) {
            return false;
        }
        int[] pattern = encodeNec(address16, command);
        irManager.transmit(CARRIER_HZ, pattern);
        return true;
    }

    /**
     * Builds the alternating mark/space microsecond pattern for a 32-bit NEC frame.
     */
    public static int[] encodeNec(int address16, int command) {
        int addrLow = address16 & 0xFF;
        int addrHigh = (address16 >> 8) & 0xFF;
        int cmd = command & 0xFF;
        int cmdInv = (~cmd) & 0xFF;

        // 2 (leader) + 32 * 2 (bits) + 1 (trailing mark) = 67 intervals
        int[] pattern = new int[67];
        int idx = 0;

        pattern[idx++] = LEADER_MARK_US;
        pattern[idx++] = LEADER_SPACE_US;

        int[] bytes = {addrLow, addrHigh, cmd, cmdInv};
        for (int b : bytes) {
            for (int bit = 0; bit < 8; bit++) {
                pattern[idx++] = BIT_MARK_US;
                boolean isOne = ((b >> bit) & 1) == 1;
                pattern[idx++] = isOne ? ONE_SPACE_US : ZERO_SPACE_US;
            }
        }

        pattern[idx] = BIT_MARK_US;
        return pattern;
    }
}
