package Oceanus.Tv.ITvFunctionInterface;

/**
 * Created by xeasy on 2017/1/11.
 */

public interface IVGA {
    public abstract boolean setVgaAutoAdjust();
    public abstract boolean setVgaHPosition(int ucPosition);
    public abstract boolean setVgaVPosition(int ucPosition);
    public abstract boolean setVgaPhase(int ucValue);
    public abstract boolean setVgaClock(int ucValue);
    public abstract int getVgaHPosition();
    public abstract int getVgaVPosition();
    public abstract int getVgaPhase();
    public abstract int getVgaClock();
}
