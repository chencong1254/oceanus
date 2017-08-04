package Oceanus.Tv.ITvFunctionInterface;

import Oceanus.Tv.Service.TeletextManager.TeletextManagerDefinitions.EN_TTX_CMD;

/**
 * Created by sky057509 on 2017/3/10.
 */
public interface ITeletext {
    public abstract boolean EnableTeletext(boolean bIsEnable);
    public abstract boolean IsTeletextExist();
    public abstract boolean TeletextPassCmd(EN_TTX_CMD Cmd);
    public abstract boolean IsTeletextStart();
}
