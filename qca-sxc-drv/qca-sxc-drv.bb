DESCRIPTION = "Kernel driver for SDC and SAC"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

SOC = "${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-sxc-drv \
	   "
PACKAGES += "kernel-module-qca-sac-drv kernel-module-qca-sdc-drv"

DEPENDS:append = "virtual/kernel qca-mmap-telemetry"

S = "${WORKDIR}/qca-sxc-drv"

MODULE_EXTRA_SYMBOLS = "${STAGING_INCDIR}/qca-mmap-telemetry/Module.symvers"

SXC_MAKE_OPTS += "qca-sac-drv=y  \
			qca-sdc-drv=y \
			"
EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-mmap-telemetry \
                "

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" ${SXC_MAKE_OPTS} \
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
	install -m 0644 ${S}/sac/qca-sac-drv${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/sdc/qca-sdc-drv${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-sxc-drv
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-sxc-drv/Module.symvers
}
