package liquer.alchemy.alembic;

public interface Timer {
    Timer stop();
    Timer stop(String stopMessage);
    int getLap();
    long total();
}
