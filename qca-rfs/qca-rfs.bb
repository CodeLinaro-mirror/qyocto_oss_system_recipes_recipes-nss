DESCRIPTION = "QCA Receive Flow Steering Kernel Module"
SECTION = "kernel/module"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://Makefile;md5=9b105acd8d4380fb8f780118eb7c1e4e"

inherit module

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

FILES_${PN}="/etc/init.d"

SRC_URI = "file://qca-rfs \
	   file://qrfs.init \
	   "

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-rfs"

PACKAGES += "kernel-module-qca-rfs"

do_configure() {
}

do_compile() {
	make -C "${STAGING_KERNEL_DIR}" 'KDIR="${STAGING_KERNEL_DIR}"' \
             'ARCH=${KARCH}' \
	     'CROSS_COMPILE=${TARGET_PREFIX}' \
	     'KERNEL_VERSION="${KERNEL_VERSION}"' \
             'CC=${KERNEL_CC}' 'M=${S}' modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 qrfs${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-rfs
	install -m 0644 ${S}/rfs_dev.h ${D}${includedir}/qca-rfs/rfs_dev.h
	install -D -m 0755 ${WORKDIR}/qrfs.init ${D}/etc/init.d/qrfs
}
