DESCRIPTION = "Kernel driver for NSS PPE core driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

FILES_${PN} = "/usr/bin"

SRC_URI = "file://qca-nss-ppe/ \
	   "

PACKAGES += "kernel-module-qca-nss-ppe "

DEPENDS = "virtual/kernel qca-ssdk-nohnat "

S = "${WORKDIR}/qca-nss-ppe/"
SSDK_STG_INCDIR = "${STAGING_INCDIR}/qca-ssdk"

NSS_PPE_MODULES += ""

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/init \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		"

do_compile() {
	unset LDFLAGS
	make -C  "${STAGING_KERNEL_BUILDDIR}" ${NSS_PPE_MODULES} \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		KBUILD_EXTRA_SYMBOLS="${SSDK_STG_INCDIR}/Module.symvers" \
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

KERNEL_MODULE_AUTOLOAD += "qca-nss-ppe"
