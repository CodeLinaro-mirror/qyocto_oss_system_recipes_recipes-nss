DESCRIPTION = "NSS Dataplane"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

SOC="${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"


FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-dp \
	   "

DEPENDS = "virtual/kernel bc-native qca-ssdk-nohnat"

S = "${WORKDIR}/qca-nss-dp"
SSDK_STG_INCDIR = "${STAGING_INCDIR}/qca-ssdk"

PACKAGES += "kernel-module-qca-nss-dp"

do_compile() {
	unset LDFLAGS
	install -m 0644 ${S}/hal/soc_ops/${SOC_TYPE}/nss_${SOC_TYPE}.h ${S}/exports/nss_dp_arch.h
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE='${TARGET_PREFIX}' \
		ARCH='${KARCH}' \
		M="${S}" \
		EXTRA_CFLAGS="-I${SSDK_STG_INCDIR}" \
		KBUILD_EXTRA_SYMBOLS="${SSDK_STG_INCDIR}/Module.symvers" \
		SoC='${SOC_TYPE}' \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 qca-nss-dp${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-dp
	install -m 0644 exports/* ${D}${includedir}/qca-nss-dp/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-dp/Module.symvers
}

FILES_${PN}-dev = "${includedir}/qca-nss-dp"
INSANE_SKIP_${PN} = "dev"
KERNEL_MODULE_AUTOLOAD += "qca-nss-dp"
