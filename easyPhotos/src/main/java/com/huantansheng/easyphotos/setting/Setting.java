package com.huantansheng.easyphotos.setting;

import android.view.View;

import com.huantansheng.easyphotos.engine.ImageEngine;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * EasyPhotos的设置值
 * Created by huan on 2017/10/24.
 */

public class Setting {
    public static int minWidth = 1;
    public static int minHeight = 1;
    public static long minSize = 1;
    public static int count = 1;
    public static WeakReference<View> photosAdView = null;
    public static WeakReference<View> albumItemsAdView = null;
    public static boolean photoAdIsOk = false;
    public static boolean albumItemsAdIsOk = false;
    public static ArrayList<Photo> selectedPhotos = new ArrayList<>();
    public static boolean showOriginalMenu = false;
    public static boolean originalMenuUsable = false;
    public static String originalMenuUnusableHint = "";
    public static boolean selectedOriginal = false;
    public static String fileProviderAuthority = null;
    public static boolean isShowCamera = false;
    public static boolean onlyStartCamera = false;
    public static boolean showPuzzleMenu = true;
    public static boolean showGif = false;
    public static boolean showVideo = false;
    public static boolean showCleanMenu = true;
    public static ImageEngine imageEngine = null;


    public static void clear() {
        minWidth = 1;
        minHeight = 1;
        minSize = 1;
        count = 1;
        photosAdView = null;
        albumItemsAdView = null;
        photoAdIsOk = false;
        albumItemsAdIsOk = false;
        selectedPhotos.clear();
        showOriginalMenu = false;
        originalMenuUsable = false;
        originalMenuUnusableHint = "";
        selectedOriginal = false;
        isShowCamera = false;
        onlyStartCamera = false;
        showPuzzleMenu = true;
        showGif = false;
        showVideo = false;
        showCleanMenu = true;
    }

    public static boolean hasPhotosAd() {
        return photosAdView != null && photosAdView.get() != null;
    }

    public static boolean hasAlbumItemsAd() {
        return albumItemsAdView != null && albumItemsAdView.get() != null;
    }
}
