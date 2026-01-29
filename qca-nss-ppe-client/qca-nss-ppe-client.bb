DESCRIPTION = "Kernel driver for NSS PPE core driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

CLEANBROKEN = "1"

OVERRIDES:append = ":qca-nss-ppe-vlan-mgr:qca-nss-ppe-bridge-mgr:qca-nss-pppoe-mgr:qca-nss-ppe-lag-mgr:qca-nss-ppe-dsa-mgr"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-ppe \
	   file://files \
	   "

PACKAGES += "kernel-module-qca-nss-ppe-client "

DEPENDS = "virtual/kernel qca-ssdk-nohnat nat46 qca-ovsmgr qca-nss-ppe qca-nss-ppe-vp"
RDEPEND-{PN} = "qca-ssdk-nohnat"
RDEPENDS-qca-nss-ppe-vlan-mgr = "qca-nss-ppe bonding"
RDEPENDS-qca-nss-ppe-bridge-mgr = "qca-nss-ppe qca-nss-ppe-vlan-mgr bonding qca-ovsmgr"
RDEPENDS-qca-nss-pppoe-mgr  = "qca-nss-ppe pppoe bonding"
RDEPENDS-qca-nss-ppe-lag-mgr   = "qca-nss-ppe qca-nss-ppe-vlan-mgr bonding"
RDEPENDS-qca-nss-ppe-dsa-mgr   = "qca-nss-ppe qca-nss-ppe-vlan-mgr"

S = "${WORKDIR}/qca-nss-ppe"
SSDK_STG_INCDIR = "${STAGING_INCDIR}/qca-ssdk"
NAT46_STG_INCDIR = "${STAGING_INCDIR}/nat46"

NSS_PPE_MODULES:append:qca-nss-ppe-bridge-mgr = " bridge-mgr=y \
						  NSS_PPE_BRIDGE_MGR_OVS_ENABLE=y \
						  "
NSS_PPE_MODULES:append:qca-nss-ppe-vlan-mgr  = " vlan-mgr=y"
NSS_PPE_MODULES:append:qca-nss-ppe-lag-mgr = " lag-mgr=y"
NSS_PPE_MODULES:append:qca-nss-ppe-dsa-mgr = " dsa-mgr=y"
NSS_PPE_MODULES:append:qca-nss-pppoe-mgr = " pppoe-mgr=y"

PPPOE_MAKE_OPTS:${SOC} += "PPPOE_MGR_FE_PPE_ENABLE=y "

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/init \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		-I${STAGING_INCDIR}/qca-ovsmgr \
		-I${STAGING_INCDIR}/ \
		"

EXTRA_CFLAGS:append = "${@' -DNSS_VLAN_MGR_WLANIF_DST_XLATE_SUPPORT' if d.getVar('CONFIG_KERNEL_IPQ_MEM_PROFILE', True) != '256' else ''}"

CONFIG_FLAGS ?= "${CONFIG_TARGET_ipq95xx_generic_QRDK_Open}${CONFIG_TARGET_ipq95xx_ipq95xx_32_QRDK_Open}${CONFIG_TARGET_ipq53xx_generic_QRDK_Open}${CONFIG_TARGET_ipq53xx_ipq53xx_32_QRDK_Open}${CONFIG_TARGET_ipq54xx_generic_QRDK_Open}${CONFIG_TARGET_ipq54xx_ipq54xx_32_QRDK_Open}"

EXTRA_CFLAGS:append = "${@' -DNSS_PPE_BRIDGE_MGR_FDB_DISABLE' if 'y' in d.getVar('CONFIG_FLAGS', True) else ''}"

MODULE_EXTRA_SYMBOLS ="${SSDK_STG_INCDIR}/Module.symvers ${NAT46_STG_INCDIR}/Module.symvers \
		       ${STAGING_INCDIR}/qca-ovsmgr/Module.symvers \
		       ${STAGING_INCDIR}/qca-nss-ppe/Module.symvers ${STAGING_INCDIR}/qca-nss-ppe-vp/Module.symvers"

module_conf_qca-nss-ppe-bridge-mgr += "options qca-nss-ppe-bridge-mgr ovs_enabled=0"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C  "${STAGING_KERNEL_BUILDDIR}" ${NSS_PPE_MODULES} \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
		SoC='${SOC_TYPE}' \
		${PPPOE_MAKE_OPTS} \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-ppe-client
	install -m 0644 ${S}/exports/* ${D}${includedir}/qca-nss-ppe-client/
	install -m 0644 ${S}/drv/exports/* ${D}${includedir}/qca-nss-ppe-client/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe-client/Module.symvers
}

do_install:append:qca-nss-ppe-vlan-mgr() {
	install -m 0644 ${S}/clients/vlan/qca-nss-ppe-vlan${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

do_install:append:qca-nss-ppe-bridge-mgr() {
	install -d ${D}${bindir}
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/files/qca-nss-ppe-bridge-mgr.service ${D}${systemd_unitdir}/system/
	install -m 0644 ${S}/clients/bridge/qca-nss-ppe-bridge-mgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0755 ${WORKDIR}/files/qca-nss-ppe-bridge-mgr.init ${D}${bindir}/qca-nss-ppe-bridge-mgr
}

do_install:append:qca-nss-pppoe-mgr() {
	install -m 0644 ${S}/clients/pppoe/qca-nss-pppoe-mgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

do_install:append:qca-nss-ppe-lag-mgr() {
	install -m 0644 ${S}/clients/lag/qca-nss-ppe-lag${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

do_install:append:qca-nss-ppe-dsa-mgr() {
	install -m 0644 ${S}/clients/dsa/qca-nss-ppe-dsa-mgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

FILES:${PN} = " ${bindir}/qca-nss-ppe-bridge-mgr \
		${systemd_unitdir}/system/qca-nss-ppe-bridge-mgr.service"

SYSTEMD_SERVICE:${PN} += "qca-nss-ppe-bridge-mgr.service"

KERNEL_MODULE_AUTOLOAD:append:qca-nss-ppe-vlan-mgr = " qca-nss-ppe-vlan"
KERNEL_MODULE_AUTOLOAD:append:qca-nss-ppe-bridge-mgr = " qca-nss-ppe-bridge-mgr"
KERNEL_MODULE_PROBECONF += "qca-nss-ppe-bridge-mgr"
KERNEL_MODULE_AUTOLOAD:append:qca-nss-pppoe-mgr = " qca-nss-pppoe-mgr"
KERNEL_MODULE_AUTOLOAD:append:qca-nss-ppe-lag-mgr = " qca-nss-ppe-lag"
KERNEL_MODULE_AUTOLOAD:append:qca-nss-ppe-dsa-mgr = " qca-nss-ppe-dsa-mgr"
