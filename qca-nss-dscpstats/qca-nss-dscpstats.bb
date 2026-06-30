DESCRIPTION = "Kernel driver for NSS DSCP STATS"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

SOC = "${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-dscpstats/ \
	   file://${THISDIR}/files \
	   "
PACKAGES += "kernel-module-qca-nss-dscpstats"

DEPENDS = "virtual/kernel qca-nss-ecm"

S = "${WORKDIR}/qca-nss-dscpstats"

EXTRA_CFLAGS += "-I${S}/exports \
                -I${STAGING_INCDIR}/libnl3 \
                -I${STAGING_INCDIR}/qca-nss-ecm \
		"
MODULE_EXTRA_SYMBOLS = "${STAGING_INCDIR}/qca-nss-ecm/Module.symvers"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" ${STATDSCP_MAKE_OPTS}\
	CROSS_COMPILE="${TARGET_PREFIX}" \
	ARCH="${KARCH}" \
	M="${S}" \
	EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
	KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
	SoC='${SOC_TYPE}' \
	modules
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/${THISDIR}/files/qca-nss-dscpstats.init ${D}${bindir}/qca-nss-dscpstats
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/${THISDIR}/files/qca-nss-dscpstats.service ${D}${systemd_unitdir}/system/
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/qca-nss-dscpstats${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-dscpstats
	install -m 0644 ${S}/exports/* ${D}${includedir}/qca-nss-dscpstats/
}

FILES:${PN} = " ${bindir}/qca-nss-dscpstats \
		${systemd_unitdir}/system/qca-nss-dscpstats.service"

SYSTEMD_SERVICE:${PN} += "qca-nss-dscpstats.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"
