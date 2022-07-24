#!/system/bin/sh
#Governor
#Little
chmod 644 /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor
echo 'ondemand' > /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu0/cpufreq/scaling_governor
chmod 644 /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor
echo 'ondemand' > /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu1/cpufreq/scaling_governor
chmod 644 /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor
echo 'ondemand' > /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu2/cpufreq/scaling_governor
chmod 644 /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor
echo 'ondemand' > /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu3/cpufreq/scaling_governor
#Big
chmod 644 /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor
echo 'powersave' > /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu4/cpufreq/scaling_governor
chmod 644 /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor
echo 'powersave' > /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu5/cpufreq/scaling_governor
chmod 644 /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor
echo 'powersave' > /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu6/cpufreq/scaling_governor
#Prime
chmod 644 /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor
echo 'powersave' > /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor
chmod 444 /sys/devices/system/cpu/cpu7/cpufreq/scaling_governor
#Offline-Cores
#Big
chmod 644 /sys/devices/system/cpu/cpu6/online
echo '1' > /sys/devices/system/cpu/cpu6/online
chmod 444 /sys/devices/system/cpu/cpu6/online
#Prime
chmod 644 /sys/devices/system/cpu/cpu7/online
echo '0' > /sys/devices/system/cpu/cpu7/online
chmod 444 /sys/devices/system/cpu/cpu7/online
#I/O
echo 'noop' > /sys/block/sda/queue/scheduler
#Cpusets
echo '0-3' > /dev/cpuset/foreground/cpus
echo '0-6' > /dev/cpuset/top-app/cpus
#Stune
echo '202' > /dev/stune/background/schedtune.util.max
echo '390' > /dev/stune/foreground/schedtune.util.max
echo '415' > /dev/stune/top-app/schedtune.util.max
#Sched-walt
echo '1' > /proc/sys/kernel/sched_use_walt_cpu_util
echo '1' > /proc/sys/kernel/sched_use_walt_task_util
#Touchpanel
echo '0' > /proc/touchpanel/game_switch_enable
echo '0' > /proc/touchpanel/sensitive_level
echo '5' > /proc/touchpanel/smooth_level
#Limiters
echo '0' >  /proc/perfmgr/syslimiter/syslimiter_force_disable
#PROC/SYS
echo '71' > /proc/sys/kernel/boost_task_threshold
echo '0' > /proc/sys/kernel/slide_boost_enabled
echo '0' > /proc/sys/kernel/launcher_boost_enabled
#Sched
echo '1' > /sys/devices/system/cpu/eas/enable
#GED
echo '-1' > /sys/kernel/ged/hal/gpu_boost_level
echo '0' > /sys/module/ged/parameters/enable_cpu_boost
echo '100' > /sys/module/ged/parameters/gpu_idle
echo '0' > /sys/module/ged/parameters/enable_gpu_boost
echo '0' > /sys/module/ged/parameters/gx_game_mode