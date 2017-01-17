DESCRIPTION = "NSS Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files/:"

SRC_URI = "file://qca-nss-drv \
	   file://01-add-clean.patch \
	   "

DEPENDS = "virtual/kernel qca-nss-gmac"

S = "${WORKDIR}/qca-nss-drv"

PACKAGES += "kernel-module-nss-drv"
FILES_${PN}-dev = "/usr/include/*"
INSANE_SKIP_${PN} = "dev"

do_clean() {
	true
}

do_compile() {
	make -C  "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="arm-poky-linux-gnueabi-" \
		ARCH="arm" \
		SUBDIRS="${S}" \
		EXTRA_CFLAGS="-I${STAGING_INCDIR}/qca-nss-gmac" \
		modules
}
do_install() {
	install -d ${D}/${includedir}/qca-nss-drv
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 qca-nss-drv${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 exports/* ${D}/${includedir}/qca-nss-drv/
}

KERNEL_MODULE_AUTOLOAD += "qca-nss-drv"
