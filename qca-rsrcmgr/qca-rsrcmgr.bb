DESCRIPTION = "Kernel driver for QCA Net Standby driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-rsrcmgr/driver/qca-net-standby \
           "
PACKAGES += "kernel-module-qca-net-standby "

DEPENDS += "virtual/kernel qca-nss-ppe qca-nss-ppe-rule qca-ssdk-nohnat qca-wifi glib-openssl qca-hostapd bc-native qca-nss-dp"
RDEPENDS_${PN} += "qca-nss-ppe qca-nss-dp qca-ssdk-nohnat"

S = "${WORKDIR}/qca-rsrcmgr/driver/qca-net-standby"

TARGET_CFLAGS += "-DCONFIG_NETSTANDBY=1 \
                  -DNETSTANDBY_DEBUG_LEVEL=3 \
                  -I${STAGING_INCDIR}/qca-wifi \
                  -I${STAGING_INCDIR}/qca-nss-ppe \
                  -I${STAGING_INCDIR}/qca-nss-dp \
                  -I${STAGING_INCDIR}/qca-nss-ppe-rule \
                  -DRM_QCA_PROP"

PPE_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ppe"
DP_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-dp"
SSDK_STG_INCDIR = "${STAGING_INCDIR}/qca-ssdk"
NAT46_STG_INCDIR = "${STAGING_INCDIR}/nat46"
RULE_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ppe-rule"

MODULE_EXTRA_SYMBOLS = "${PPE_STG_INCDIR}/Module.symvers \
                        ${RULE_STG_INCDIR}/Module.symvers \
                        ${DP_STG_INCDIR}/Module.symvers \
                        ${SSDK_STG_INCDIR}/Module.symvers \
                        ${NAT46_STG_INCDIR}/Module.symvers"

do_configure(){
        true
}

do_compile() {
	make -C "${STAGING_KERNEL_BUILDDIR}" \
	CROSS_COMPILE="${TARGET_PREFIX}" \
	ARCH="${KARCH}" \
	M="${S}" \
	EXTRA_CFLAGS="${TARGET_CFLAGS}" \
	KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
	SoC="${SOC_TYPE}" \
	modules
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
    install -m 0644 ${S}/qca-net-standby${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

KERNEL_MODULE_AUTOLOAD:${PN} = " qca-rsrcmgr"
