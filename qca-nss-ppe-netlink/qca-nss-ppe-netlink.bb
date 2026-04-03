DESCRIPTION = "Kernel driver for NSS PPE-netlink core driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC = "${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-nss-ppe"

PACKAGES += "kernel-module-qca-nss-ppe-netlink"

DEPENDS = " virtual/kernel qca-nss-ppe qca-nss-ppe-rule"
RDEPENDS-${PN}:ipq52xx:append = " qca-nss-dp"
RDEPENDS-${PN}:ipq52xx_64:append = " qca-nss-dp"
RDEPENDS-${PN}:ipq96xx:append = " qca-nss-dp"
RDEPENDS-${PN}:ipq96xx_64:append = " qca-nss-dp"
S = "${WORKDIR}/qca-nss-ppe"

NSS_PPE_MODULES:${SOC} += "netlink=y"
NSS_PPE_MODULES:ipq52xx:append = " PPE_TUN_RPS_ENABLED=y"
NSS_PPE_MODULES:ipq52xx_64:append = " PPE_TUN_RPS_ENABLED=y"
NSS_PPE_MODULES:ipq96xx:append = " PPE_TUN_RPS_ENABLED=y"
NSS_PPE_MODULES:ipq96xx_64:append = " PPE_TUN_RPS_ENABLED=y"


MODULE_EXTRA_SYMBOLS = " \
		${STAGING_INCDIR}/qca-nss-ppe/Module.symvers \
		${STAGING_INCDIR}/qca-nss-ppe-rule/Module.symvers \
		${STAGING_INCDIR}/qca-nss-dp/Module.symvers \
		"

EXTRA_CFLAGS+= " \
                -I${STAGING_INCDIR}/qca-nss-ppe \
                -I${STAGING_INCDIR}/qca-ssdk \
                -I${STAGING_INCDIR}/qca-ssdk/init \
                -I${STAGING_INCDIR}/qca-ssdk/fal \
		-I${STAGING_INCDIR}/qca-nss-ppe/drv/ \
		-I${STAGING_INCDIR}/qca-nss-dp \
		"

do_configure() {
        true
}

do_compile() {
        unset LDFLAGS
        make -C "${STAGING_KERNEL_BUILDDIR}" ${NSS_PPE_MODULES} \
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
	install -m 0644 ${S}/netlink/qca-nss-ppe-netlink${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
        install -d ${D}${includedir}/qca-nss-ppe-netlink
        install -m 0644 ${S}/drv/exports/* ${D}${includedir}/qca-nss-ppe-netlink
        install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe-netlink/Module.symvers
}

KERNEL_MODULE_AUTOLOAD += "qca-nss-ppe-netlink"
