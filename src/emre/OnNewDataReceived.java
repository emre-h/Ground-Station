package emre;

import java.util.EventListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author emre
 */
public interface OnNewDataReceived extends EventListener{
    public void onNewDataReceived(int[] data);
}