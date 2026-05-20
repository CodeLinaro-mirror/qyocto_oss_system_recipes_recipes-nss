DESCRIPTION = "Flow Identification lite version"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH = "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-nsm"

S = "${WORKDIR}/qca-nss-nsm/fls"

DEPENDS:append += "virtual/kernel qca-nss-ecm"

PACKAGES += "kernel-module-qca-nss-fls-lite"

FLS_LITE_MAKE_OPTS = "FLS_LITE_ENABLE=y"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-nss-ecm \
		"

MODULE_EXTRA_SYMBOLS += " \
		${STAGING_INCDIR}/qca-nss-ecm/Module.symvers \
		"

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" ${FLS_LITE_MAKE_OPTS} \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="${KARCH}" \
		M="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/qca-nss-fls-lite${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-fls-lite
	install -m 0644 ${S}/*.h ${D}${includedir}/qca-nss-fls-lite/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-fls-lite/Module.symvers
}