package com.example.hjj.readcardtools;

/**
 * Created by hjj on 2017-05-25.
 */

public class UsbStdInterface {
    private native static  int libusb_init();
    private native static  void libusb_exit();
    private native static int libusb_get_device_list();
    private native static int libusb_get_device_descriptor(int dev,byte[] data);
    private native static int libusb_free_device_list();
}
