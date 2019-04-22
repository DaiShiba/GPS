package tigerapplication2.yomogi.co.jp.gps.Activity_Fragment;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * 位置情報検知のON/OFF検知用LiveData
 * MonitorFragment内のイベントを検知し、NavigationTopActivityに通知を行う
 * */
public class MonitorViewModel extends ViewModel {
    MutableLiveData<Boolean> location = new MutableLiveData();
}
