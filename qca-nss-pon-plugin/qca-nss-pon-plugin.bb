DESCRIPTION = "Kernel driver for PON plugin"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-pon-plugin"

S = "${WORKDIR}/qca-nss-pon-plugin"

PACKAGES += "kernel-module-qca-nss-pon-plugin"

PON_PLUGIN_MAKE_OPTS = "qca-nss-pon-plugin=m"

EXTRA_CFLAGS += "-I${S}/exports"

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" ${PON_PLUGIN_MAKE_OPTS} \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		SoC="${SOC_TYPE}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 qca-nss-pon-plugin${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-pon-plugin
	install -m 0644 ${S}/exports/* ${D}${includedir}/qca-nss-pon-plugin/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-pon-plugin/Module.symvers
}

FILES:${PN}-dev = "${includedir}/qca-nss-pon-plugin"
