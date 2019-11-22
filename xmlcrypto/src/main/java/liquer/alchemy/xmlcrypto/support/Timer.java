package liquer.alchemy.xmlcrypto.support;

public interface Timer {
    Timer stop();
    Timer stop(String stopMessage);
    int getLap();
    long total();
}
