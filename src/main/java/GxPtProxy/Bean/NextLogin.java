package GxPtProxy.Bean;

public class NextLogin {
    private String packet="";
    private String random="";
    private String second="true";
    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }
    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}
