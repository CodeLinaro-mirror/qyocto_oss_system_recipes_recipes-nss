DESCRIPTION = "Kernel driver for NSS Wifi Plugins"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://qca_nss_wifi_plugins_init.c;md5=c97e382cdf49d551dad9b065c96e2bab"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-wifi-plugins/ \
	"

PACKAGES += "kernel-module-qca-wifi-plugins "

DEPENDS = "virtual/kernel qca-emesh-sp qca-wifi"
RDEPENDS-${PN}:append = " qca-emesh-sp \
			qca-wifi"

S = "${WORKDIR}/qca-wifi-plugins"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/emesh-sp \
		-I${STAGING_INCDIR}/qca-wifi \
		-I${STAGING_INCDIR}/ \
		"

MODULE_EXTRA_SYMBOLS ="${STAGING_INCDIR}/emesh-sp/Module.symvers \
			${STAGING_INCDIR}/qca-wifi/Module.symvers"

do_configure() {
	true
}

setup_build_variables() {
	cp -af ${TOPDIR}/../wifi/qca-wifi/component_dev/qca_sawf/qca_sawf_if.h ${STAGING_INCDIR}; \
}

do_compile() {
	setup_build_variables
	unset LDFLAGS
	make -C  "${STAGING_KERNEL_BUILDDIR}" \
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
	install -m 0644 qca-wifi-plugins${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-wifi-plugins
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-wifi-plugins/Module.symvers
}

KERNEL_MODULE_AUTOLOAD:${PN} = " qca-wifi-plugins"
