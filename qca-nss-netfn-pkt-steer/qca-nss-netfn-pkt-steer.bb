DESCRIPTION = "NSS network function for pkt steer"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-netfn/"

S = "${WORKDIR}/qca-nss-netfn"

PACKAGES += "kernel-module-qca-nss-netfn-pkt-steer"

DEPENDS = "virtual/kernel"

PKT_STEER_MAKE_OPTS = "pkt_steer=y"

EXTRA_CFLAGS += " \
		-I${S}/offload/exports \
		"
do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" ${PKT_STEER_MAKE_OPTS} \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/offload/pkt_steer/qca-nss-netfn-pkt-steer${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-netfn
	install -m 0644 ${S}/exports/* ${D}${includedir}/qca-nss-netfn/
	install -m 0644 ${S}/offload/exports/* ${D}${includedir}/qca-nss-netfn/
}

KERNEL_MODULE_AUTOLOAD += "qca-nss-netfn-pkt-steer"