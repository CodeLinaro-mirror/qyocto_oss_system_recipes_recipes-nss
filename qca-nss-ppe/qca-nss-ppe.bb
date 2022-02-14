DESCRIPTION = "Kernel driver for NSS PPE bridge and vlan managers"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

FILES_${PN} += "/usr/bin"

SRC_URI = "file://qca-nss-ppe/ \
	   "

PACKAGES += "kernel-module-qca-nss-ppe"

DEPENDS = "virtual/kernel bc-native qca-ssdk-nohnat "

RDEPENDS-qca-nss-drv-bridge-mgr += "bonding qca-nss-drv-vlan-mgr"

S = "${WORKDIR}/qca-nss-ppe/"
SSDK_STG_INCDIR = "${STAGING_INCDIR}/qca-ssdk"

NSS_PPE_MODULES = ""
NSS_PPE_MODULES += "vlan-mgr=y"
NSS_PPE_MODULES += "bridge-mgr=y"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/init \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		"

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
		KBUILD_EXTRA_SYMBOLS="${SSDK_STG_INCDIR}/Module.symvers ${DP_STG_INCDIR}/Module.symvers" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/clients/vlan/qca-nss-ppe-vlan${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/clients/bridge/qca-nss-ppe-bridge-mgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-ppe
	install -m 0644 ${S}/drv/exports/* ${D}${includedir}/qca-nss-ppe/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe/Module.symvers
}

KERNEL_MODULE_AUTOLOAD += "qca-nss-ppe-vlan"
KERNEL_MODULE_AUTOLOAD += "qca-nss-ppe-bridge-mgr"
