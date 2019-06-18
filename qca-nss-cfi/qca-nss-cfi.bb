DESCRIPTION = "NSS CFI Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

SOC_TYPE="${@bb.data.getVar('SOC_FAMILY', d, 1).split(':')[1]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-cfi \
	   "

PROVIDES += "qca-nss-cfi-cryptoapi qca-nss-cfi-ocf qca-nss-ipsec"

DEPENDS = "virtual/kernel qca-nss-crypto qca-nss-drv"
#RDEPENDS_${PN} = "qca-nss-crypto crypto-authenc crypto-ocf"

S = "${WORKDIR}/qca-nss-cfi"

PACKAGES += "kernel-module-qca-nss-cfi-cryptoapi kernel-module-qca-nss-cfi-ocf kernel-module-qca-nss-ipsec"

do_configure() {
	true
}

CFI_OCF_DIR = "ocf/v2.0"
CFI_CRYPTOAPI_DIR = "cryptoapi/v2.0"
CFI_IPSEC_DIR = "ipsec/v2.0"

EXTRA_CFLAGS += "\
		-DCONFIG_NSS_DEBUG_LEVEL=4 \
		-I${STAGING_INCDIR}/qca-nss-crypto \
		-I${STAGING_INCDIR}/qca-nss-clients \
		-I${STAGING_KERNEL_DIR}/crypto \
		-I${STAGING_KERNEL_DIR}/crypto/ocf \
		-I${STAGING_INCDIR}/qca-nss-drv \
		"

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		SUBDIRS="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		CFI_CRYPTOAPI_DIR="${CFI_CRYPTOAPI_DIR}" \
		CFI_OCF_DIR="${CFI_OCF_DIR}" \
		CFI_IPSEC_DIR="${CFI_IPSEC_DIR}" \
		SoC="${SOC_TYPE}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/${CFI_CRYPTOAPI_DIR}/qca-nss-cfi-cryptoapi${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/${CFI_OCF_DIR}/qca-nss-cfi-ocf${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/${CFI_IPSEC_DIR}/qca-nss-ipsec${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}

	install -d ${D}${includedir}/qca-nss-cfi
	install -m 0644 ${S}/${CFI_CRYPTOAPI_DIR}/../exports/* ${D}/${includedir}/qca-nss-cfi/.
}

FILES_${PN}-dev = "${includedir}/qca-nss-cfi"
INSANE_SKIP_${PN} = "dev"
KERNEL_MODULE_AUTOLOAD += "qca-nss-cfi-cryptoapi qca-nss-cfi-ocf qca-nss-ipsec"
