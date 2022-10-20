package com.example.myapplication3.fragments.HomeFragment;

import androidx.lifecycle.ViewModel;

import com.example.myapplication3.fragment_data_models.Cpu;
import com.example.myapplication3.fragment_data_models.Display;
import com.example.myapplication3.fragment_data_models.Gpu;
import com.example.myapplication3.fragment_data_models.Memory;
import com.example.myapplication3.fragment_data_models.Misc;
import com.example.myapplication3.fragment_data_models.Power;

public class FragmentPersistObject extends ViewModel {
    private Cpu.Params cpuParams;
    private Gpu.Params gpuParams;
    private Display.Params displayParams;
    private Memory.Params memoryParams;
    private Misc.Params miscParams;
    private Power.Params powerParams;

    public void setCpuParams(Cpu.Params cpuParams) {
        this.cpuParams = cpuParams;
    }

    public Cpu.Params getCpuParams() {
        if(cpuParams != null) {
            return cpuParams;
        }
        else {
            return cpuParams = new Cpu.Params();
        }
    }

    public Gpu.Params getGpuParams() {
        if(gpuParams != null) {
            return gpuParams;
        }
        else {
            return gpuParams = new Gpu.Params();
        }
    }

    public Display.Params getDisplayParams() {
        if(displayParams != null) {
            return displayParams;
        }
        else {
            return displayParams = new Display.Params();
        }
    }

    public Memory.Params getMemoryParams() {
        if(memoryParams != null) {
            return memoryParams;
        }
        else {
            return memoryParams = new Memory.Params();
        }
    }

    public Misc.Params getMiscParams() {
        if(miscParams != null) {
            return miscParams;
        }
        else {
            return miscParams = new Misc.Params();
        }
    }

    public Power.Params getPowerParams() {
        if(powerParams != null) {
            return powerParams;
        }
        else {
            return powerParams = new Power.Params();
        }
    }
}
