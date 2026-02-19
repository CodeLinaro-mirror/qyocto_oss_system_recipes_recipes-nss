DESCRIPTION = "Recipe for qca-nss-eip, qca-nss-eip-crypto, and qca-nss-eip-ipsec drivers"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

OVERRIDES:append = ":qca-nss-eip-crypto:qca-nss-eip-ipsec:"

SOC = "${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-eip \
	file://files \
	"

PACKAGES += "kernel-module-qca-nss-eip kernel-module-qca-nss-eip-crypto kernel-module-qca-nss-eip-ipsec"

DEPENDS:append += " virtual/kernel qca-nss-ppe"
DEPENDS:qca-nss-eip-ipsec += " qca-nss-ecm qca-nss-ppe-vp"
RDEPENDS:ipq95xx:${PN} = " qca-nss-fw-eip-al"
RDEPENDS:ipq95xx_64:${PN} = " qca-nss-fw-eip-al"
RDEPENDS-qca-nss-eip-crypto  = " qca-nss-eip authenc"
RDEPENDS-qca-nss-eip-ipsec   = " qca-nss-eip"

S = "${WORKDIR}/qca-nss-eip"

NSS_EIP_DIR:ipq95xx = "eip197"
NSS_EIP_DIR:ipq95xx_64 = "eip197"

NSS_EIP_DIR:ipq53xx = "eip196"
NSS_EIP_DIR:ipq53xx_64 = "eip196"

NSS_EIP_DIR:ipq54xx = "eip196"
NSS_EIP_DIR:ipq54xx_64 = "eip196"

NSS_EIP_DIR:ipq52xx = "eip196"
NSS_EIP_DIR:ipq52xx_64 = "eip196"

NSS_EIP_DIR:ipq96xx = "eip197_v2"
NSS_EIP_DIR:ipq96xx_64 = "eip17_v2"

NSS-EIP-MODULES = "eip_ipsec=m"

eip_crypto = "m"

PPE_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ppe"
ECM_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ecm"
PPE_VP_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ppe-vp"

EXTRA_CFLAGS += "-I${STAGING_INCDIR}/qca-nss-ppe \
		-I${STAGING_INCDIR}/qca-nss-ecm \
		-I${STAGING_INCDIR}/qca-nss-ppe-vp \
		"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
        make -C  "${STAGING_KERNEL_BUILDDIR}" ${NSS-EIP-MODULES} \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		KBUILD_EXTRA_SYMBOLS="${PPE_STG_INCDIR}/Module.symvers ${ECM_STG_INCDIR}/Module.symvers ${PPE_VP_STG_INCDIR}/Module.symvers" \
		NSS_EIP_DIR=${NSS_EIP_DIR} \
		eip_crypto=${eip_crypto} \
		SoC="${SOC_TYPE}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/driver/${NSS_EIP_DIR}/qca-nss-eip${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/clients/crypto/qca-nss-eip-crypto${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/clients/ipsec/qca-nss-eip-ipsec${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/files/eip_dump.sh ${D}${bindir}/eip_dump.sh
	install -m 0755 ${WORKDIR}/files/qca-nss-ipsec ${D}${bindir}/qca-nss-ipsec
	install -d ${D}${includedir}/qca-nss-eip
	install -m 0644 ${S}/driver/exports/* ${D}${includedir}/qca-nss-eip/.
	install -m 0644 ${S}/clients/exports/* ${D}${includedir}/qca-nss-eip/.
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-eip/Module.symvers
}

FILES:${PN} = " \
		${bindir}/eip_dump.sh \
		${bindir}/qca-nss-ipsec \
		"
FILES:${PN}-dev = "${includedir}/qca-nss-eip "

INSANE_SKIP:${PN} = "dev"
KERNEL_MODULE_AUTOLOAD += "qca-nss-eip "
