DESCRIPTION = "Kernel driver for NSS Speedtest UDP STD DRV"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://nss-speedtest/nss-udp-st-drv \
	"

PACKAGES += "kernel-module-nss-udp-st-drv "

DEPENDS = "virtual/kernel qca-nss-ppe-vp qca-nss-ppe-tun"
RDEPENDS-${PN}:append = " qca-nss-ppe-vp \
			qca-nss-ppe-tun"

S = "${WORKDIR}/nss-speedtest/nss-udp-st-drv"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-nss-ppe \
		-I${STAGING_INCDIR}/qca-nss-ppe-vp \
		-I${STAGING_INCDIR}/qca-nss-ppe-tun \
		"

MODULE_EXTRA_SYMBOLS ="${STAGING_INCDIR}/qca-nss-ppe-vp/Module.symvers \
			${STAGING_INCDIR}/qca-nss-ppe-tun/Module.symvers \
			${STAGING_INCDIR}/qca-nss-ppe/Module.symvers"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C  "${STAGING_KERNEL_BUILDDIR}" NSS_UDP_ST_DRV_VP=y \
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
	install -m 0644 nss-udp-st${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/nss-udp-st-drv
	install -m 0644 ${S}/exports/* ${D}${includedir}/nss-udp-st-drv/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/nss-udp-st-drv/Module.symvers
}

KERNEL_MODULE_AUTOLOAD:${PN} = " nss-udp-st"
