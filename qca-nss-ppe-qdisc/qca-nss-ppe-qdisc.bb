DESCRIPTION = "Kernel module for configuring schedulers and shapers in PPE"
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

PACKAGES += "kernel-module-qca-nss-ppe-qdisc"

DEPENDS = "virtual/kernel qca-nss-ppe"
RDEPEND-{PN} = "qca-nss-ppe"

S = "${WORKDIR}/qca-nss-ppe/"
PPE_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ppe"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/init \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		-I${STAGING_INCDIR}/ \
		"

MODULE_EXTRA_SYMBOLS ="${PPE_STG_INCDIR}/Module.symvers"

NSS_MAKE_OPTS = "ppe-qdisc=y"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C  "${STAGING_KERNEL_BUILDDIR}" ${NSS_MAKE_OPTS} \
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
	install -m 0644 ${S}/drv/ppe_qdisc/qca-nss-ppe-qdisc${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-ppe-qdisc
	install -m 0644 ${S}/exports/* ${D}${includedir}/qca-nss-ppe-qdisc/
	install -m 0644 ${S}/drv/exports/* ${D}${includedir}/qca-nss-ppe-qdisc/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe-qdisc/Module.symvers
}

KERNEL_MODULE_AUTOLOAD += "qca-nss-ppe-qdisc"
