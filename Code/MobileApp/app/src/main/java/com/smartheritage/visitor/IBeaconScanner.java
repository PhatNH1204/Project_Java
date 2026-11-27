package com.smartheritage.visitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.ParcelUuid;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.UUID;

public final class IBeaconScanner {

    public interface Listener {
        void onBeacon(Beacon beacon);
        void onScanError(int errorCode);
    }

    public static final class Beacon {
        public final String uuid;
        public final int major;
        public final int minor;
        public final int rssi;

        Beacon(String uuid, int major, int minor, int rssi) {
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;
            this.rssi = rssi;
        }

        public String key() {
            return uuid + ":" + major + ":" + minor;
        }
    }

    private final Context context;
    private final Listener listener;
    private BluetoothLeScanner scanner;
    private final ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            byte[] manufacturerData = result.getScanRecord() == null ? null
                    : result.getScanRecord().getManufacturerSpecificData(0x004c);
            Beacon beacon = parse(manufacturerData, result.getRssi());
            if (beacon != null) listener.onBeacon(beacon);
        }

        @Override
        public void onScanFailed(int errorCode) {
            listener.onScanError(errorCode);
        }
    };

    public IBeaconScanner(Context context, Listener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    public boolean start() {
        BluetoothManager manager = context.getSystemService(BluetoothManager.class);
        BluetoothAdapter adapter = manager == null ? null : manager.getAdapter();
        if (adapter == null || !adapter.isEnabled()) return false;
        scanner = adapter.getBluetoothLeScanner();
        if (scanner == null) return false;
        scanner.startScan(callback);
        return true;
    }

    public void stop() {
        if (scanner != null) {
            scanner.stopScan(callback);
            scanner = null;
        }
    }

    static Beacon parse(byte[] data, int rssi) {
        if (data == null || data.length < 23 || data[0] != 0x02 || data[1] != 0x15) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        buffer.position(2);
        long most = buffer.getLong();
        long least = buffer.getLong();
        String uuid = new UUID(most, least).toString().toLowerCase(Locale.ROOT);
        int major = Short.toUnsignedInt(buffer.getShort());
        int minor = Short.toUnsignedInt(buffer.getShort());
        return new Beacon(uuid, major, minor, rssi);
    }
}
