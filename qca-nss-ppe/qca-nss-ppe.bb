DESCRIPTION = "Kernel driver for NSS PPE core driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
OVERRIDES:append = ":qca-nss-ppe-vlan-mgr:qca-nss-ppe-bridge-mgr:qca-nss-ppe-pppoe-mgr:qca-nss-ppe-lag-mgr:"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-nss-ppe/ \
	   "

PACKAGES += "kernel-module-qca-nss-ppe "

DEPENDS = "virtual/kernel qca-ssdk-nohnat nat46 qca-ovsmgr"
RDEPEND-{PN} = "qca-ssdk-nohnat"
RDEPENDS-qca-nss-ppe-vlan-mgr = "qca-nss-ppe bonding"
RDEPENDS-qca-nss-ppe-bridge-mgr = "qca-nss-ppe qca-nss-ppe-vlan-mgr bonding qca-ovsmgr"
RDEPENDS-qca-nss-ppe-pppoe-mgr  = "qca-nss-ppe pppoe bonding"
RDEPENDS-qca-nss-ppe-lag-mgr   = "qca-nss-ppe qca-nss-ppe-vlan-mgr bonding"

S = "${WORKDIR}/qca-nss-ppe"
SSDK_STG_INCDIR = "${STAGING_INCDIR}/qca-ssdk"
NAT46_STG_INCDIR = "${STAGING_INCDIR}/nat46"

NSS_PPE_MODULES:append:qca-nss-ppe-bridge-mgr = " bridge-mgr=y \
						  NSS_PPE_BRIDGE_MGR_OVS_ENABLE=y \
						  "
NSS_PPE_MODULES:append:qca-nss-ppe-vlan-mgr  = " vlan-mgr=y"
NSS_PPE_MODULES:append:qca-nss-ppe-pppoe-mgr = " pppoe-mgr=y"
NSS_PPE_MODULES:append:qca-nss-ppe-lag-mgr = " lag-mgr=y"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/init \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		-I${STAGING_INCDIR}/qca-ovsmgr \
		-I${STAGING_INCDIR}/ \
		"

MODULE_EXTRA_SYMBOLS ="${SSDK_STG_INCDIR}/Module.symvers ${NAT46_STG_INCDIR}/Module.symvers \
		       ${STAGING_INCDIR}/qca-ovsmgr/Module.symvers"

module_conf_qca-nss-ppe-bridge-mgr += "options qca-nss-ppe-bridge-mgr ovs_enabled=1"

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
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/drv/ppe_drv/qca-nss-ppe${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-ppe
	install -m 0644 ${S}/exports/* ${D}${includedir}/qca-nss-ppe/
	install -m 0644 ${S}/drv/exports/* ${D}${includedir}/qca-nss-ppe/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe/Module.symvers
}

do_install:append:qca-nss-ppe-vlan-mgr() {
	install -m 0644 ${S}/clients/vlan/qca-nss-ppe-vlan${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

do_install:append:qca-nss-ppe-bridge-mgr() {
	install -m 0644 ${S}/clients/bridge/qca-nss-ppe-bridge-mgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

do_install:append:qca-nss-ppe-pppoe-mgr() {
	install -m 0644 ${S}/clients/pppoe/qca-nss-ppe-pppoe-mgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

do_install:append:qca-nss-ppe-lag-mgr() {
	install -m 0644 ${S}/clients/lag/qca-nss-ppe-lag${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}


KERNEL_MODULE_AUTOLOAD:${PN} = " qca-nss-ppe"
KERNEL_MODULE_AUTOLOAD:append:qca-nss-ppe-vlan-mgr = " qca-nss-ppe-vlan"
KERNEL_MODULE_AUTOLOAD:append:qca-nss-ppe-bridge-mgr = " qca-nss-ppe-bridge-mgr"
KERNEL_MODULE_PROBECONF += "qca-nss-ppe-bridge-mgr"
KERNEL_MODULE_AUTOLOAD:append:qca-nss-ppe-pppoe-mgr = " qca-nss-ppe-pppoe-mgr"
KERNEL_MODULE_AUTOLOAD:append:qca-nss-ppe-lag-mgr = " qca-nss-ppe-lag"
