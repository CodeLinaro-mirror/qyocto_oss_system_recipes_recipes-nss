DESCRIPTION = "Kernel driver for NSS PPE core driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-nss-ppe/ \
	   "

PACKAGES += "kernel-module-qca-nss-ppe "

DEPENDS = "virtual/kernel qca-ssdk-nohnat nat46 qca-ovsmgr"
RDEPEND-{PN} = "qca-ssdk-nohnat"

S = "${WORKDIR}/qca-nss-ppe"
SSDK_STG_INCDIR = "${STAGING_INCDIR}/qca-ssdk"
NAT46_STG_INCDIR = "${STAGING_INCDIR}/nat46"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/init \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		-I${STAGING_INCDIR}/qca-ovsmgr \
		-I${STAGING_INCDIR}/ \
		"

MODULE_EXTRA_SYMBOLS ="${SSDK_STG_INCDIR}/Module.symvers ${NAT46_STG_INCDIR}/Module.symvers \
			${STAGING_INCDIR}/qca-ovsmgr/Module.symvers"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C  "${STAGING_KERNEL_BUILDDIR}"  ppe-drv=y PPE_IPSEC_ENABLE=y PPE_TUN_ENABLE=y \
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
	install -m 0644 ${S}/netlink/include/* ${D}${includedir}/qca-nss-ppe/
	install -m 0644 ${S}/drv/exports/* ${D}${includedir}/qca-nss-ppe/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe/Module.symvers
}

KERNEL_MODULE_AUTOLOAD:${PN} = " qca-nss-ppe"
