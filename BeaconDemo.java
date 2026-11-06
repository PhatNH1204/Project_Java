public class BeaconDemo {

    // Một beacon đọc được: UUID, Major, Minor, RSSI
    static class Beacon {
        String uuid;
        int major;
        int minor;
        int rssi;

        Beacon(String uuid, int major, int minor, int rssi) {
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;
            this.rssi = rssi;
        }
    }

    // Đổi RSSI thành khoảng cách (mét). txPower = RSSI đo ở 1m, thường là -59
    static double tinhKhoangCach(int rssi) {
        int txPower = -59;
        return Math.pow(10, (txPower - rssi) / 20.0);
    }

    // Xác định vùng: Immediate / Near / Far
    static String xacDinhVung(double khoangCach) {
        if (khoangCach < 0.5) {
            return "Immediate";
        } else if (khoangCach < 3) {
            return "Near";
        } else {
            return "Far";
        }
    }

    public static void main(String[] args) {
        String uuid = "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0";

        // Giả lập visitor đi lại gần beacon A (RSSI càng lớn càng gần)
        Beacon[] cacLanScan = {
            new Beacon(uuid, 1, 1, -85),
            new Beacon(uuid, 1, 1, -68),
            new Beacon(uuid, 1, 1, -50)
        };

        for (Beacon b : cacLanScan) {
            double d = tinhKhoangCach(b.rssi);
            String vung = xacDinhVung(d);
            System.out.printf("Beacon %d-%d | RSSI = %d | %.2f m | %s%n",
                    b.major, b.minor, b.rssi, d, vung);
        }
    }
}
