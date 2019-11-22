package liquer.alchemy.xmlcrypto.crypto.opt;

public class HashOptions extends EncoderOptions {

    private boolean zeroTerminated;

    public HashOptions() {
        super();
        zeroTerminated = false;
    }

    public boolean isZeroTerminated() {
        return zeroTerminated;
    }

    public void setZeroTerminated(boolean zeroTerminated) {
        this.zeroTerminated = zeroTerminated;
    }



}
