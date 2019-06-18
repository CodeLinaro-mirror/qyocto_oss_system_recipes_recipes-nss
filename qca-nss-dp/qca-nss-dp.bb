DESCRIPTION = "NSS Dataplane"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

SOC_TYPE="${@bb.data.getVar('SOC_FAMILY', d, 1).split(':')[1]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-nss-dp \
	   "

DEPENDS = "virtual/kernel qca-ssdk-nohnat"

S = "${WORKDIR}/qca-nss-dp"

PACKAGES += "kernel-module-qca-nss-dp"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE='${TARGET_PREFIX}' \
		ARCH='${KARCH}' \
		SUBDIRS="${S}" \
		EXTRA_CFLAGS="-I${STAGING_INCDIR}/qca-ssdk" \
		SoC='${SOC_TYPE}' \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 qca-nss-dp${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-dp
	install -m 0644 exports/* ${D}/${includedir}/qca-nss-dp/
}

FILES_${PN}-dev = "${includedir}/qca-nss-dp"
INSANE_SKIP_${PN} = "dev"
KERNEL_MODULE_AUTOLOAD += "qca-nss-dp"
