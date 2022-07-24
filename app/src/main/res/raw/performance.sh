#!/system/bin/sh

#Online-Cores
#Big
chmod 644 /sys/devices/system/cpu/cpu6/online
echo '1' > /sys/devices/system/cpu/cpu6/online
chmod 444 /sys/devices/system/cpu/cpu6/online
#Prime
chmod 644 /sys/devices/system/cpu/cpu7/online
echo '1' > /sys/devices/system/cpu/cpu7/online
chmod 444 /sys/devices/system/cpu/cpu7/online
#Governor
#Little
chmod 644 /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor
echo 'schedutil' > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor
chmod 644 /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor
echo 'schedutil' > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor
#Big
chmod 644 /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor
echo 'schedutil' > /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor
chmod 644 /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor
echo 'schedutil' > /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor
chmod 644 /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor
echo 'schedutil' > /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor
chmod 644 /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor
#Prime
echo 'schedutil' > /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor
#I/O
echo 'cfq' > /sys/block/sda/queue/scheduler
#Cpusets
echo '0-7' > /dev/cpuset/foreground/cpus
echo '0-7' > /dev/cpuset/top-app/cpus
#Stune
echo '1024' > /dev/stune/background/schedtune.util.max
echo '1024' > /dev/stune/foreground/schedtune.util.max
echo '1024' > /dev/stune/top-app/schedtune.util.max
#Sched-Walt
echo '0' > /proc/sys/kernel/sched_use_walt_cpu_util
echo '0' > /proc/sys/kernel/sched_use_walt_task_util
#Touchpanel
echo '1' > /proc/touchpanel/game_switch_enable
echo '5' > /proc/touchpanel/sensitive_level
echo '1' > /proc/touchpanel/smooth_level
#Limiters
echo '1' >  /proc/perfmgr/syslimiter/syslimiter_force_disable
#Kernel
echo '51' > /proc/sys/kernel/boost_task_threshold
echo '1' > /proc/sys/kernel/slide_boost_enabled
echo '1' > /proc/sys/kernel/launcher_boost_enabled
#Sched
echo '0' > /sys/devices/system/cpu/eas/enable
#GED
echo '4' > /sys/kernel/ged/hal/gpu_boost_level
echo '1' > /sys/module/ged/parameters/enable_cpu_boost
echo '50' > /sys/module/ged/parameters/gpu_idle
echo '1' > /sys/module/ged/parameters/enable_gpu_boost
echo '1' > /sys/module/ged/parameters/gx_game_mode