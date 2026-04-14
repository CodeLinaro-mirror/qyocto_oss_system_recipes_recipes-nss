#!/bin/sh
# Copyright (c) Qualcomm Technologies, Inc. and/or its subsidiaries.
# SPDX-License-Identifier: ISC

#
# usage:
# enable ESP SPI acceleration configuration
#       ./lib/ecm_esp_spi_accel.sh start
# disable ESP SPI acceleration configuration
#       ./lib/ecm_esp_spi_accel.sh stop
# show ESP SPI acceleration configuration status
#       ./lib/ecm_esp_spi_accel.sh status
#

set_sysctls() {
        local value="$1"

        echo "$value" > /proc/sys/net/netfilter/nf_conntrack_esp_enabled
        echo "$value" > /proc/sys/net/ecm/esp_spi_passthrough_enable
        echo "$value" > /sys/sfe/esp_spi_passthrough_feature
        echo "$value" > /proc/sys/ppe/ppe_drv/ppe_drv_ipsec_passth_en
}

start() {
        set_sysctls 1
        echo "ecm_esp_spi_accel: ESP SPI acceleration enabled"
}

stop() {
        set_sysctls 0
        echo "ecm_esp_spi_accel: ESP SPI acceleration disabled"
}

status() {
        echo "ecm_esp_spi_accel: current status"
        echo "nf_conntrack_esp_enabled=$(cat /proc/sys/net/netfilter/nf_conntrack_esp_enabled)"
        echo "esp_spi_passthrough_enable=$(cat /proc/sys/net/ecm/esp_spi_passthrough_enable)"
        echo "esp_spi_passthrough_feature=$(cat /sys/sfe/esp_spi_passthrough_feature)"
        echo "ppe_drv_ipsec_passth_en=$(cat /proc/sys/ppe/ppe_drv/ppe_drv_ipsec_passth_en)"
}

usage() {
        echo "Usage: $0 {start|stop|status}"
        exit 1
}

case "$1" in
start)
        start
;;
stop)
        stop
;;
status)
        status
;;
*)
        usage
;;
esac
