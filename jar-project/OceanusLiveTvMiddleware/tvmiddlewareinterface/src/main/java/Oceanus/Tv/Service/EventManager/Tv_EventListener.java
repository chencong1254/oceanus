package Oceanus.Tv.Service.EventManager;

/**
 * Created by heji@skyworth.com on 2016/7/7.
 */
public abstract class Tv_EventListener
{
    private String Name = " ";

    public Tv_EventListener(String name) {
        this.Name = name;
    }

    public Tv_EventListener() {

    }

    protected abstract void onEvnet(Tv_EventInfo info);
    public String getListenerName()
    {
        return this.Name;
    };
}
