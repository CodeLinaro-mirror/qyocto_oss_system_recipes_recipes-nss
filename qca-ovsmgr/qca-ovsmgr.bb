DESCRIPTION = "Kernel driver for NSS OpenVSwitch manager"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

SOC = "${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-ovsmgr \
	   "
PACKAGES += "kernel-module-qca-ovsmgr"

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-ovsmgr"

EXTRA_CFLAGS += " \
		-I${STAGING_KERNEL_DIR}/net/openvswitch \
		-I${S}/exports \
		"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" ${OVSMGR_MAKE_OPTS} \
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
	install -m 0644 ${S}/qca-ovsmgr${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-ovsmgr
	install -m 0644 ${WORKDIR}/qca-ovsmgr/exports/* ${D}${includedir}/qca-ovsmgr/.
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-ovsmgr/Module.symvers
}

KERNEL_MODULE_AUTOLOAD += "qca-ovsmgr"
