DESCRIPTION = "NSS Crypto Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

SOC_TYPE="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-crypto \
	   "

DEPENDS = "virtual/kernel qca-nss-drv"

S = "${WORKDIR}/qca-nss-crypto"

PACKAGES += "kernel-module-qca-nss-crypto kernel-module-qca-nss-crypto-tool"

do_configure() {
	true
}

NSS_CRYPTO_DIR = "v2.0"

EXTRA_CFLAGS += "\
		 -DCONFIG_NSS_DEBUG_LEVEL=4 \
		 -I${STAGING_INCDIR}/qca-nss-crypto \
		 -I${STAGING_INCDIR}/qca-nss-drv \
		 -I${S}/${NSS_CRYPTO_DIR}/include \
		 -I${S}/${NSS_CRYPTO_DIR}/src \
		 "

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		NSS_CRYPTO_DIR=${NSS_CRYPTO_DIR} \
		KBUILD_EXTRA_SYMBOLS="${STAGING_INCDIR}/qca-nss-drv/Module.symvers" \
		SoC="${SOC_TYPE}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/${NSS_CRYPTO_DIR}/src/qca-nss-crypto${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/${NSS_CRYPTO_DIR}/tool/qca-nss-crypto-tool${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}

	install -d ${D}${includedir}/qca-nss-crypto
	install -m 0644 ${S}/${NSS_CRYPTO_DIR}/include/* ${D}/${includedir}/qca-nss-crypto/.
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-crypto/Module.symvers
}

FILES_${PN}-dev = "${includedir}/qca-nss-crypto"
INSANE_SKIP_${PN} = "dev"
KERNEL_MODULE_AUTOLOAD += "qca-nss-crypto qca-nss-crypto-tool"
