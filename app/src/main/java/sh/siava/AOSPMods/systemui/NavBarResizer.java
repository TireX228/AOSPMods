package sh.siava.AOSPMods.systemui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import sh.siava.AOSPMods.XPrefs;

public class NavBarResizer implements IXposedHookLoadPackage {
    public static final String listenPackage = "com.android.systemui";
    public static boolean isEnabled = false;
    public static float sizeFactor = 1f;

    public static void updatePrefs()
    {
        isEnabled = XPrefs.Xprefs.getBoolean("GesPillWidthMod", false);
        sizeFactor = XPrefs.Xprefs.getInt("GesPillWidthModPos", 50) * .02f;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(!lpparam.packageName.equals(listenPackage)) return;

        XposedHelpers.findAndHookMethod("com.android.systemui.navigationbar.NavigationBarInflaterView", lpparam.classLoader,
                "createView", String.class, ViewGroup.class, LayoutInflater.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if(!isEnabled) return;

                        String button = (String) XposedHelpers.callMethod(param.thisObject, "extractButton", param.args[0]);
                        if(!button.equals("home_handle")) return;

                        View v = (View) param.getResult();
                        final ViewGroup.LayoutParams lp = v.getLayoutParams();
                        lp.width = Math.round(lp.width * sizeFactor);
                        v.setLayoutParams(lp);
                    }
                });
    }
}