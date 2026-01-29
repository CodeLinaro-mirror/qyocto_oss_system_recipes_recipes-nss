DESCRIPTION = "Kernel driver for NSS PPE-GRE tunnel"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC= "${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-nss-ppe/ \
	   "

PACKAGES += "kernel-module-qca-nss-ppe-gre"

DEPENDS = "virtual/kernel qca-nss-ppe-tun"

S = "${WORKDIR}/qca-nss-ppe/clients/gre/"
PPE_TUN_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ppe-tun"


string='BUILD_ID = \\"Build Id: $(shell date +'%m/%d/%y\,\ %H:%M:%S')\\"'

GRE_MGR_MAKE_OPTS:${SOC} += "GRE_MGR_FE_PPE_ENABLE=y "

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		"

MODULE_EXTRA_SYMBOLS = "${PPE_TUN_STG_INCDIR}/Module.symvers "

do_configure() {
	sed -i '3i ${string}' Makefile
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
	CROSS_COMPILE="${TARGET_PREFIX}" \
	ARCH="${KARCH}" \
	M="${S}" \
	EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
	KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
	SoC='${SOC_TYPE}' \
	${GRE_MGR_MAKE_OPTS} \
	modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/qca-nss-ppe-gre${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-ppe-gre
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe-gre/Module.symvers
}

KERNEL_MODULE_AUTOLOAD += "qca-nss-ppe-gre"
