DESCRIPTION = "Kernel driver for NSS PPE-TUN core driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC = "${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-nss-ppe"

PACKAGES += "kernel-module-qca-nss-ppe-tun"

DEPENDS = "virtual/kernel qca-nss-ppe-vp qca-nss-ppe-rule"

S = "${WORKDIR}/qca-nss-ppe/drv/ppe_tun/"
PPE_VP_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ppe-vp"

EXTRA_CFLAGS += "-I${STAGING_INCDIR}/qca-nss-ppe \
		 -DPPE_TUN_RULE_MODULE_ENABLED"

MODULE_EXTRA_SYMBOLS = "${PPE_VP_STG_INCDIR}/Module.symvers \
			${STAGING_INCDIR}/qca-nss-ppe-rule/Module.symvers"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
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
	install -m 0644 ${S}/qca-nss-ppe-tun${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-ppe-tun
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe-tun/Module.symvers
}

KERNEL_MODULE_AUTOLOAD += "qca-nss-ppe-tun"
